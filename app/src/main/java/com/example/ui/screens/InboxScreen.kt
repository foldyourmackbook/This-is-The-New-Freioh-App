package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ChatMessage
import com.example.data.model.ContactPerson
import com.example.data.model.Conversation
import com.example.ui.components.BlueVerifiedBadge
import com.example.ui.components.E2EEBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreiohViewModel

@Composable
fun InboxScreen(
    viewModel: FreiohViewModel,
    modifier: Modifier = Modifier
) {
    val conversations by viewModel.allConversations.collectAsState()
    val activeConversation by viewModel.activeConversation.collectAsState()
    val isCreatingGroup by viewModel.isCreatingGroup.collectAsState()
    val chatMessages by viewModel.currentChatMessages.collectAsState()

    if (activeConversation != null) {
        // ACTIVE 1-ON-1 OR GROUP CHAT SCREEN
        ChatThreadView(
            conversation = activeConversation!!,
            messages = chatMessages,
            onBack = { viewModel.closeConversation() },
            onSendMessage = { text -> viewModel.sendMessage(text) },
            onSendVoiceNote = { viewModel.sendMessage("Voice note (0:14)", messageType = "VOICE") },
            onStartVideoCall = { viewModel.startVideoCall(activeConversation!!) }
        )
    } else {
        // CONVERSATIONS LIST & CONTACTS DIRECTORY
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(TikTokBlack)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Direct Messages",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Create Group Chat Button
                    Button(
                        onClick = { viewModel.openCreateGroupModal() },
                        colors = ButtonDefaults.buttonColors(containerColor = TikTokMagenta),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.testTag("create_group_chat_button")
                    ) {
                        Icon(imageVector = Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Group", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // E2EE Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TikTokCardDark)
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🔒", fontSize = 16.sp)
                    Column {
                        Text(
                            text = "End-to-End Encrypted Messaging",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Messages and calls are secured. Only you and recipients have the keys.",
                            color = TikTokGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Online Contacts Stories/Avatars Row
            Text(
                text = "Active Friends",
                color = TikTokGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(viewModel.sampleContacts) { contact ->
                    ContactAvatarItem(
                        contact = contact,
                        onClick = {
                            val existing = conversations.find { it.title.contains(contact.name, ignoreCase = true) }
                            if (existing != null) {
                                viewModel.openConversation(existing)
                            } else {
                                // Create DM conversation
                                val newConv = Conversation(
                                    title = contact.name,
                                    avatarUrl = contact.avatarUrl,
                                    isGroup = false,
                                    participantHandles = listOf(contact.handle),
                                    lastMessage = "Started encrypted chat 🔒",
                                    unreadCount = 0,
                                    isOnline = contact.isOnline,
                                    isVerified = contact.isVerified
                                )
                                viewModel.openConversation(newConv)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            // Conversations List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(conversations, key = { it.id }) { conv ->
                    ConversationRow(
                        conversation = conv,
                        onClick = { viewModel.openConversation(conv) },
                        onVideoCallClick = { viewModel.startVideoCall(conv) }
                    )
                }
            }
        }
    }

    // CREATE GROUP CHAT MODAL
    if (isCreatingGroup) {
        CreateGroupChatDialog(
            contacts = viewModel.sampleContacts,
            onDismiss = { viewModel.closeCreateGroupModal() },
            onCreate = { title, selectedHandles ->
                viewModel.createGroupChat(title, selectedHandles)
            }
        )
    }
}

@Composable
private fun ContactAvatarItem(
    contact: ContactPerson,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(56.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = contact.avatarUrl,
                contentDescription = contact.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(TikTokCardDark)
            )
            if (contact.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(TikTokBlack)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(E2EEGreen)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = contact.name.split(" ").first(),
            color = TikTokLightGray,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    onClick: () -> Unit,
    onVideoCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = conversation.avatarUrl,
                    contentDescription = conversation.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(TikTokCardDark)
                )
                if (conversation.isGroup) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(TikTokMagenta),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = conversation.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (conversation.isVerified) {
                        BlueVerifiedBadge(size = 13.dp)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = conversation.lastMessage,
                        color = if (conversation.unreadCount > 0) Color.White else TikTokGray,
                        fontSize = 13.sp,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(text = "• ${conversation.lastMessageTime}", color = TikTokGray, fontSize = 11.sp)
                }
            }
        }

        // Quick Video Call Icon
        IconButton(
            onClick = onVideoCallClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(TikTokCardDark)
                .testTag("conversation_video_call_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "Video Call",
                tint = TikTokCyan,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ChatThreadView(
    conversation: Conversation,
    messages: List<ChatMessage>,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSendVoiceNote: () -> Unit,
    onStartVideoCall: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TikTokBlack)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TikTokDarkBg)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                AsyncImage(
                    model = conversation.avatarUrl,
                    contentDescription = conversation.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                )

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = conversation.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (conversation.isVerified) {
                            BlueVerifiedBadge(size = 12.dp)
                        }
                    }
                    Text(text = "🔒 E2EE Active • Online", color = E2EEGreen, fontSize = 10.sp)
                }
            }

            // Video Call Icon in Chat Top App Bar
            IconButton(
                onClick = onStartVideoCall,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TikTokMagenta.copy(alpha = 0.2f))
                    .testTag("chat_video_call_top_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Video Call",
                    tint = TikTokMagenta,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Encrypted Chat Security Notice
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TikTokCardDark.copy(alpha = 0.6f))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔒 Messages and calls are end-to-end encrypted. No one outside of this chat can read or listen to them.",
                color = TikTokGray,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe = msg.isFromMe
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMe) 16.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 16.dp
                                )
                            )
                            .background(if (isMe) TikTokMagenta else TikTokSurfaceDark)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column {
                            if (!isMe && conversation.isGroup) {
                                Text(
                                    text = msg.senderName,
                                    color = TikTokCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                            if (msg.messageType == "VOICE") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                    Text("Voice Note (0:14)", color = Color.White, fontSize = 13.sp)
                                }
                            } else {
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Input Bar
        Surface(
            color = TikTokDarkBg,
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Voice note button
                IconButton(
                    onClick = onSendVoiceNote,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TikTokSurfaceDark)
                ) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice", tint = Color.White, modifier = Modifier.size(20.dp))
                }

                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Send encrypted message...", color = TikTokGray, fontSize = 13.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TikTokSurfaceDark,
                        unfocusedContainerColor = TikTokSurfaceDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("chat_input_field")
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) TikTokMagenta else TikTokSurfaceDark)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Color.White else TikTokGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateGroupChatDialog(
    contacts: List<ContactPerson>,
    onDismiss: () -> Unit,
    onCreate: (String, List<String>) -> Unit
) {
    var groupTitle by remember { mutableStateOf("") }
    var selectedHandles by remember { mutableStateOf(setOf<String>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TikTokDarkBg,
        title = {
            Text(
                text = "Create Group Chat",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
            ) {
                OutlinedTextField(
                    value = groupTitle,
                    onValueChange = { groupTitle = it },
                    label = { Text("Group Name") },
                    placeholder = { Text("e.g. TikTok Creators Squad") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TikTokMagenta,
                        unfocusedBorderColor = TikTokGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select Participants (${selectedHandles.size})",
                    color = TikTokGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(contacts) { contact ->
                        val isSelected = selectedHandles.contains(contact.handle)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedHandles = if (isSelected) {
                                        selectedHandles - contact.handle
                                    } else {
                                        selectedHandles + contact.handle
                                    }
                                }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AsyncImage(
                                    model = contact.avatarUrl,
                                    contentDescription = contact.name,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                )
                                Column {
                                    Text(text = contact.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = contact.handle, color = TikTokGray, fontSize = 11.sp)
                                }
                            }

                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = TikTokMagenta,
                                    uncheckedColor = TikTokGray
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (groupTitle.isNotBlank() && selectedHandles.isNotEmpty()) {
                        onCreate(groupTitle, selectedHandles.toList())
                    }
                },
                enabled = groupTitle.isNotBlank() && selectedHandles.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = TikTokMagenta)
            ) {
                Text("Create Group 🔒")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TikTokGray)
            }
        }
    )
}
