<template>
    <el-scrollbar class="chatlog" dropzone="true">
      <div v-for="chat in state.chatArray" :key="chat" class="chatContent">
        {{chat}}
      </div>
      <ChatMessage
        v-for="(message, index) in state.chatArray"
        :key="index"
        :message="message"
      />
    </el-scrollbar>
    <el-divider>🐶</el-divider>
    <textarea
      class='chatinput'
      v-model='state.chatString'
      @keyup.enter='sendMessage'
    />
</template>

<style scoped>
.chatlog {
  width: 100%;
  height: calc(100% - 140px);
  background-color: white;
  padding: 9px;
  overflow: auto;
}
:deep(.el-divider) {
  margin-bottom: 2px;
}
.chatinput {
  outline-color: orangered;
  padding: 5px 5px;
  width: 100%;
  height: 70px;
  text-align: left;
}
.chatContent {
  text-align: left;
}
/* textarea 우측 하단 /// 안보이게 + 스크롤 기능 O + 스크롤바 X */
textarea {
  border: none;
  outline: none;
  -ms-overflow-style: none;
  resize: none;
}
textarea::-webkit-scrollbar {
  display: none;
}textarea:focus {
  outline: none;
}
</style>

<script>
import { onBeforeMount, reactive } from 'vue';
export default {
  name: 'ConferenceChat',
  props:{
    session:Object,
    clientData: Object,
  },
  setup(props) {
    const state = reactive({
      session: undefined,
      subscribers:[],
      myUserName: '',
      chatArray: [],
      chatString:'',
    })

    onBeforeMount(() => {
      console.log('chat.vue > props.session 🔽')
      console.log(props.session)
      state.session = props.session

      state.session.on('signal:chatSend', (event) => {
        console.log('arrive chat')
        console.log(event.data)
        console.log('After arrive chat ')
        state.chatArray.push(event.data)
      })
    })

    const sendMessage = function() {
      console.log('sendMessage > 사용자가 enter 쳤다')
      console.log(state.chatString)
      state.myUserName = props.clientData
      console.log(state.myUserName)
      const msg = {
        userName : state.myUserName,
        content : state.chatString
      }
      state.session.signal({
        data: JSON.stringify(msg),
        to:[],
        type: 'chatSend'
      })
      .then(()=> {
        console.log('Message successfully send❗')
        state.chatString='';
      })
      .catch(error => {
        console.log(error)
      })
    }
    return {state, onBeforeMount, sendMessage}
  }
  // mounted() {
  //   console.log("Chat: ", this.socket);
  //   /* chatting */
  //   this.socket.on("chat", (user, msg) => {
  //     // console.log(name, msg);
  //     var chatLog = document.getElementById("chatLog");
  //     chatLog.append(user.username + ": " + msg + "\n");
  //     chatLog.scrollTop = chatLog.scrollHeight;
  //   });
  // },
};
</script>


