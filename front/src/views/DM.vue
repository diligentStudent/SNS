<template>
  <div class="wrap">
    <div class="wrap-container-dm">
      <div class="dm-container-wrap">
        <div class="dm-container">
        </div>
      </div>
      <!-- <div class="search-dm">
        <input type="text" name="" id="" class="search-dm-input" placeholder="검색">
        <i class="fas fa-search search-img-dm"></i>
      </div> -->
    </div>
  </div>
</template>

<script>
import "../components/css/dm.css"
import { mapState, mapMutations } from 'vuex'
import firebase from 'firebase'
import axios from 'axios'


var db = firebase.firestore();

export default {
  name: 'DM',
  data() {
    return {
      lastMessage:{},
      nickname: '',
    }
  },
  mounted() {
    this.defaultDark()
    this.getLastMessage();
  },
  computed: {
    ...mapState(['flag'])
  },
  watch: {
    flag() {
      this.defaultDark()
    }
  },
  updated() {
    this.defaultDark()
  },
  methods: {
    ...mapMutations(['setDmProfileImg']),
    defaultDark() {
      const Dark = this.$cookies.get('dark')
      const HTML = document.querySelector('html')
      const wrap = document.querySelector('.wrap')
      const INPUT = document.querySelectorAll('input')
      const H3TAG = document.querySelectorAll('h3')
      const DM_TEXT = document.querySelectorAll('.dm-content-text')
      
      // const searchIMGDM = document.querySelector('.search-img-dm')
      
      if (Dark === null) {
        this.$cookies.set('dark', 'on')
      }

      if (Dark === 'off') {
        HTML.classList.add('black')
        wrap.classList.add('wrap-dark')
        for (let i=0; i<INPUT.length ; i++) {
          INPUT[i].classList.add('input-dark')
        }
        for (let i=0; i<H3TAG.length ; i++) {
          H3TAG[i].classList.add('font-dark')
        }
        for (let i=0; i<DM_TEXT.length ; i++) {
          DM_TEXT[i].classList.add('font-dark')
        }
        
        // searchIMGDM.classList.add('search-img-dm-dark')

      } else {
        HTML.classList.remove('black')
        wrap.classList.remove('wrap-dark')
        for (let i=0; i<INPUT.length ; i++) {
          INPUT[i].classList.remove('input-dark')
        }
        for (let i=0; i<H3TAG.length ; i++) {
          H3TAG[i].classList.remove('font-dark')
        }
        for (let i=0; i<DM_TEXT.length ; i++) {
          DM_TEXT[i].classList.remove('font-dark')
        }
        
        // searchIMGDM.classList.remove('search-img-dm-dark')
      }
    },
   
    getLastMessage() {
      axios.get('https://i3b304.p.sns.io/api/chat/allChatList',{
        params:{
        username: this.nickname,
        }
        }).then((data)=>{
          let ARRAY = data.data.object
          
          ARRAY.forEach(element => {
            db.collection(element.roomname).orderBy('createdAt','desc').limit(1).onSnapshot((querySnapshot)=>{

            

            let allMessages = {};
              querySnapshot.forEach(doc=>{
                allMessages = doc.data();
              })
              this.lastMessage=allMessages;
              const CONTENT_BOX = document.createElement('div')
              const H3 = document.createElement('h3')
              const H5message = document.createElement('h5')
              const H5date = document.createElement('span')
              const IMGDM = document.createElement('img')
              const DIVUNDER = document.createElement('div')
              const DIVUPPER = document.querySelector('.dm-container')

              H3.classList.add('dm-user-name')
              if (element.firstuser == this.nickname) {
                H3.innerText = element.seconduser
              } else {
                H3.innerHTML = element.firstuser
              }
              if (this.lastMessage.message) {
                if (this.lastMessage.message.length < 30) {
                  H5message.innerHTML = this.lastMessage.message
                } else {
                  H5message.innerHTML = this.lastMessage.message.substring(0, 30) + '..'
                }
              }
              if (this.lastMessage.createdAt) {
                const Time = ((new Date() - new Date(this.lastMessage.createdAt.seconds*1000)) / (1000 * 60))
  
                if (Time < 60) {
                  H5date.innerHTML = Math.floor(Time / 1) + '분전'
                } else if (Time < 60 * 24) {
                  H5date.innerHTML = Math.floor(Time / 60) + '시간전'
                } else if (Time < 60 * 24 * 7) {
                  H5date.innerHTML = Math.floor(Time / (60 * 24)) + '일전'
                } else {
                  H5date.innerHTML = Math.floor(Time / (60 * 24 * 7)) + '주전'
                }
              }

              // 이미지 aws로 상대경로 잡아 주어야 함
              // IMGDM.src = element.img
              // IMGDM.classList.add('dm-container-message-img')
              
              // IMGDM.src = "../assets/images/default-user.png"
              IMGDM.classList.add('dm-container-message-img')
              if(element.img!=null && element.img.length>0) IMGDM.setAttribute('src',element.img);
              else IMGDM.setAttribute("src", "/images/default-user.png")
              CONTENT_BOX.classList.add('dm-content-box')
              DIVUNDER.classList.add('dm-container-message')
              H5message.classList.add('dm-in-text')
              H5message.classList.add('dm-content-text')
              H3.classList.add('dm-user-name')
              H5date.classList.add('dm-in-text')
              DIVUNDER.appendChild(IMGDM)
              DIVUNDER.appendChild(CONTENT_BOX)
              CONTENT_BOX.appendChild(H3)
              H3.appendChild(H5date)
              CONTENT_BOX.appendChild(H5message)
              // DIVUNDER.appendChild(H3)
              // DIVUNDER.appendChild(H5message)
              // DIVUNDER.appendChild(H5date)


              if (DIVUPPER) {
                DIVUPPER.appendChild(DIVUNDER)
              }
              DIVUNDER.addEventListener('click', () => {
                let Next;
                if (element.firstuser == this.nickname) {
                  Next = element.seconduser
                } else {
                  Next = element.firstuser
                }
                axios.get('https://i3b304.p.sns.io/api/chat/existroom',{
                  params:{
                    firstuser: this.nickname,
                    seconduser: Next
                  }
                  }).then(()=>{
                    if(element.img!=null && element.img.length>0) this.setDmProfileImg(element.img)
                    else this.setDmProfileImg('');
                    this.$router.push(`/directmessage/${element.roomname}/${Next}`).catch(()=>{})
                  })
                    .catch(
                    )
              })
              if (DIVUPPER) {
                this.defaultDark()
              }
              
              
            });
          });

        })
        .catch(
        )
    },
  },
  created(){
    let nickdata = this.$cookies.get('auth-nickname')
    let uri = nickdata;
    let uri_enc = encodeURIComponent(uri);
    let uri_dec = decodeURIComponent(uri_enc);
    let res = uri_dec;
    this.nickname = res
  }

}
</script>
