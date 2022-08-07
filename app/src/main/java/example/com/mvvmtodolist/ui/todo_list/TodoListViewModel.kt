package example.com.mvvmtodolist.ui.todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import example.com.mvvmtodolist.data.Todo
import example.com.mvvmtodolist.data.TodoRepository
import example.com.mvvmtodolist.ui.util.Routes
import example.com.mvvmtodolist.ui.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    val todos = repository.getTodos()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deletedTodo: Todo? = null

    fun onEvent(event: TodoListEvent) {
        when (event) {
            is TodoListEvent.OnTodoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_OR_EDIT_TODO + "?todoId=${event.todo.id}"))
            }
            is TodoListEvent.OnAddTodoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_OR_EDIT_TODO))
            }
            is TodoListEvent.OnDoneChange -> {
                viewModelScope.launch {
                    /*
                     * Need to update isDone in DB however,
                     * we know that it will be replaced
                     * when we insert the same id
                     */
                    repository.insertTodo(
                        event.todo.copy(
                            isDone = event.isDone
                        )
                    )
                }
            }
            is TodoListEvent.OnDeleteTodoClick -> {
                viewModelScope.launch {
                    repository.deleteTodo(event.todo)
                    deletedTodo = event.todo
                    sendUiEvent(
                        UiEvent.ShowSnackbar(
                            message = "${event.todo.title} has been deleted",
                            action = "Undo"
                        )
                    )
                }
            }
            is TodoListEvent.OnUndoDeleteClick -> {
                deletedTodo?.let { todo ->
                    viewModelScope.launch {
                        repository.insertTodo(todo)
                    }
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}