package example.com.mvvmtodolist.ui.add_or_edit_todo

sealed class AddOrEditTodoEvent {
    data class OnTitleChange(val title: String): AddOrEditTodoEvent()
    data class OnDescriptionChange(val description: String): AddOrEditTodoEvent()
    object OnSaveTodoClick: AddOrEditTodoEvent()
}
