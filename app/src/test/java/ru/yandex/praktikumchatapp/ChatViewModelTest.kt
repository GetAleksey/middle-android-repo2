import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send message should update messages with MyMessage`() = runTest {
        val messageText = "TestMessage"

        viewModel.messages.test {
            viewModel.sendMyMessage(messageText)

            val expectedMessage = Message.MyMessage(messageText)
            val actualMessage = expectMostRecentItem().last()

            assertEquals(expectedMessage, actualMessage)
        }
    }

    @Test
    fun `send message should be thread safe`() = runTest {
        val messagesToSend = (1..100).map { "Message $it" }

        messagesToSend
            .map { messageText ->
                launch {
                    viewModel.sendMyMessage(messageText)
                }
            }
            .joinAll()

        viewModel.messages.test {
            val expectedMessages = messagesToSend
                .map { messageText -> Message.MyMessage(messageText) }
                .toSet()

            val actualMessages = expectMostRecentItem().toSet()

            assertEquals(expectedMessages, actualMessages)
        }
    }
}