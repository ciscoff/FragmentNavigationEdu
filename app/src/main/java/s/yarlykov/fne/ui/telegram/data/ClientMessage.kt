package s.yarlykov.fne.ui.telegram.data

class ClientMessage(message : String, date : String) : ChatMessage(MessageType.Client, message, date)