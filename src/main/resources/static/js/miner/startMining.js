let startMining = new Vue({
    el: '#startMining',
    data: {
        connect: false,
        subscribe: false,
        websocket: null,

        // auth
        account: null,
        userCode: null,

        // user
        createTime: null,
        commonCoin: 0,
        seniorCoin: 0,
        rareCoin: 0,
        fewCoin: 0
    },
    // 钩子函数
    mounted: function() {
        this.webSocket();
    },
    methods: {
        webSocket: function() {
            userCode = localStorage.getItem("userCode");
            if (userCode === null || userCode === undefined) {
                return false;
            }
            let wsServerUrl = wsServer + "?userCode=" + userCode;
            let obj = this;
            this.websocket = new WebSocket(wsServerUrl);
            this.websocket.onopen = function (event) {
                console.log(event);
            };

            this.websocket.onmessage = function (msg) {
                let result = JSON.parse(msg.data);
                console.log(msg);
                switch (result.code) {
                    case NOT_AUTH:
                        console.log("用户未授权.");
                        return false;
                    case IS_AUTH:
                        obj.connect = true;
                        console.log("用户已授权.");
                        break;
                    case USER_PROFILE:
                        console.log("获取到用户详情：" + result.data);
                        let socketData = JSON.parse(result.data);
                        obj.createTime = socketData.createTime;
                        obj.commonCoin = socketData.commonCoin;
                        obj.seniorCoin = socketData.seniorCoin;
                        obj.rareCoin = socketData.rareCoin;
                        obj.fewCoin = socketData.fewCoin;
                        obj.userCode = socketData.userCode;
                        break;
                    default:
                        return true;
                }
            };
        },

        userAuth: function() {
            let obj = this;
            const request = {
                mobile: this.account,
                userCode: this.userCode
            };

            this.sendPost(userAuthUrl, request).then(result => {
                if (result) {
                    isAuth = true;
                    obj.connect = true;
                    successAlert('授权成功.');
                    localStorage.setItem("userCode", obj.userCode);
                    this.webSocket();
                }
            });
        },

        // 发送post请求
        sendPost: function(url, params) {
            return axios.post(serverUrl + url, params, {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(resp => {
                if (resp.data.code === 1) {
                    return Promise.resolve(true);
                } else {
                    errorAlertContainText(resp.data.message);
                    return Promise.resolve(false);
                }
            }).catch(error => {
                errorAlertContainText('发生了错误.', error);
                return Promise.resolve(false);
            });
        }
    }
});