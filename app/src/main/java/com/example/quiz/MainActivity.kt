package com.example.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quiz.ui.theme.QuizTheme
import com.example.quiz.ui.theme.QuizTheme
import kotlinx.coroutines.delay


data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(navController = navController)
                        }
                        composable("quiz") {
                            QuizScreen(navController = navController)
                        }
                        composable("results/{score}") { backStackEntry ->
                            val score = backStackEntry.arguments?.getString("score")?.toInt() ?: 0
                            ResultScreen(navController = navController, score = score)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to the Quiz App!", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
        Button(onClick = { navController.navigate("quiz") }) {
            Text(text = "Start Quiz")
        }
    }
}

@Composable
fun QuizScreen(navController: NavController) {
    val questions = remember {
        listOf(
            Question("What color is the sky on a clear day?", listOf("Blue", "Green", "Yellow", "Red"), 0),
            Question("How many legs does a cat have?", listOf("2", "4", "6", "8"), 1),
            Question("Which animal says 'meow'?", listOf("Dog", "Cat", "Cow", "Horse"), 1),
            Question("How many days are there in a week?", listOf("5", "6", "7", "8"), 2),
            Question("Which season is cold?", listOf("Summer", "Spring", "Winter", "Autumn"), 2)
        )
    }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var timerValue by remember { mutableStateOf(10) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var quizFinished by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = currentQuestionIndex, key2 = quizFinished) {
        if (!quizFinished) {
            timerValue = 10
            selectedOptionIndex = null
            while (timerValue > 0) {
                delay(1000L)
                timerValue--
            }
            if (timerValue == 0 && !quizFinished) {

                if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                } else {
                    quizFinished = true
                    navController.navigate("results/$score") {
                        popUpTo("quiz") { inclusive = true }
                    }
                }
            }
        }
    }

    if (quizFinished) {
        Text("Quiz Finished! Navigating to results...")
    } else if (currentQuestionIndex < questions.size) {
        val currentQuestion = questions[currentQuestionIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Question ${currentQuestionIndex + 1}/${questions.size}",
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Time: $timerValue",
                fontSize = 24.sp,
                color = if (timerValue <= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = currentQuestion.text,
                fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            currentQuestion.options.forEachIndexed { index, option ->
                Button(
                    onClick = {
                        if (selectedOptionIndex == null) {
                            selectedOptionIndex = index
                            if (index == currentQuestion.correctAnswerIndex) {
                                score++
                            }
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                            } else {
                                quizFinished = true
                                navController.navigate("results/$score") {
                                    popUpTo("quiz") { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    enabled = selectedOptionIndex == null
                ) {
                    Text(text = option, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun ResultScreen(navController: NavController, score: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Quiz Finished!", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
        Text(text = "Your Score: $score correct answers", fontSize = 20.sp, modifier = Modifier.padding(8.dp))
        Button(onClick = {
            navController.popBackStack("home", inclusive = false)
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }) {
            Text(text = "Restart Quiz")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QuizTheme {
        Text("App Preview")
    }
}
