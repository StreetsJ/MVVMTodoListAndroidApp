package example.com.mvvmtodolist.ui.layouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import example.com.mvvmtodolist.data.Todo
import example.com.mvvmtodolist.ui.todo_list.TodoItem
import example.com.mvvmtodolist.ui.todo_list.TodoListEvent
import example.com.mvvmtodolist.ui.todo_list.TodoListViewModel
import example.com.mvvmtodolist.ui.util.UiEvent
import kotlinx.coroutines.flow.collect

@Preview
@Composable
fun TodoListPagePreview() {
//    TodoListPage()
}

@Composable
fun TodoListPage(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val todos = viewModel.todos.collectAsState(initial = listOf())
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(TodoListEvent.OnUndoDeleteClick)
                    }
                }
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(TodoListEvent.OnAddTodoClick)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add todo")
            }
        }
    ) {
        TodoList(
            todos = todos.value,
            onEvent = viewModel::onEvent,
            viewModel = viewModel
        )
    }
}

@Composable
fun TodoList(todos: List<Todo>, onEvent: (TodoListEvent) -> Unit, viewModel: TodoListViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(todos) { todo ->
            TodoItem(
                todo = todo,
                onEvent = onEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.onEvent(TodoListEvent.OnTodoClick(todo))
                    }
                    .padding(16.dp)
            )
        }
    }
}
