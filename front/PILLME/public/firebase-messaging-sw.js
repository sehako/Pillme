
          const firebaseConfig = {
            apiKey: "undefined",
            authDomain: "undefined",
            projectId: "undefined",
            storageBucket: "undefined",
            messagingSenderId: "undefined",
            appId: "undefined"
          };

          importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-app-compat.js');
          importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-messaging-compat.js');

          firebase.initializeApp(firebaseConfig);
          
          const messaging = firebase.messaging();
        