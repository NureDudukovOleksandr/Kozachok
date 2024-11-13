package dudukov.nure.kozachok.Sign_in


data class SignInResult(
    val data: String,
    var errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val email: String?,
    val profilePictureUrl: String?
)