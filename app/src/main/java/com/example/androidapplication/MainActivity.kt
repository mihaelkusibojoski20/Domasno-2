package com.example.androidapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.androidapplication.ui.theme.AndroidApplicationTheme
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {

    private var dictionaryMap: MutableMap<String, String> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load dictionary once
        dictionaryMap = loadDictionary(this)

        setContent {
            AndroidApplicationTheme {
                DictionaryScreen(this, dictionaryMap)
            }
        }
    }
}

@Composable
fun DictionaryScreen(context: Context, dictionary: MutableMap<String, String>) {

    var searchText by remember { mutableStateOf("") }
    var englishText by remember { mutableStateOf("") }
    var macedonianText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }

    var dictionaryState by remember { mutableStateOf(dictionary.toMutableMap()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {

        // ===== BLACK STATUS BAR =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Black)
        )

        // ===== HEADER =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFBDBDBD))
                .padding(12.dp)
        ) {
            Text("Macedonian - English Dictionary")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(16.dp)) {

            // SEARCH
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search word") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val word = searchText.trim().lowercase()

                    val found = dictionaryState.entries.find {
                        it.key.lowercase() == word ||
                                it.value.lowercase() == word
                    }

                    resultText = if (found != null) {
                        "${found.key} = ${found.value}"
                    } else {
                        "Word not found"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = resultText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ADD NEW WORD
            OutlinedTextField(
                value = englishText,
                onValueChange = { englishText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("English word") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = macedonianText,
                onValueChange = { macedonianText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Macedonian word") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (englishText.isNotBlank() && macedonianText.isNotBlank()) {

                        dictionaryState[englishText.trim()] = macedonianText.trim()

                        saveWord(context, englishText, macedonianText)

                        englishText = ""
                        macedonianText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text("Add Word")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LIST
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFCC80))
                .padding(16.dp)
        ) {

            Text(
                text = "All Words",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            dictionaryState.forEach { (eng, mk) ->
                Text("$eng - $mk")
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// READ FROM assets
fun loadDictionary(context: Context): MutableMap<String, String> {

    val map = mutableMapOf<String, String>()

    try {
        val inputStream = context.assets.open("dictionary.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.forEachLine { line ->
            val parts = line.split(",")
            if (parts.size == 2) {
                map[parts[0].trim()] = parts[1].trim()
            }
        }

        reader.close()

    } catch (e: Exception) {
        e.printStackTrace()
    }

    return map
}

// SAVE TO INTERNAL STORAGE
fun saveWord(context: Context, english: String, macedonian: String) {
    try {
        val text = "$english, $macedonian\n"
        context.openFileOutput("user_dictionary.txt", Context.MODE_APPEND)
            .use {
                it.write(text.toByteArray())
            }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}