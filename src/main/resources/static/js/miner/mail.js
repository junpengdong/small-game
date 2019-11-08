let mail = new Vue({
    el: '#mail',
    data: {
        mobile: null,
        name: null,
        email: null,
        goal: null
    },
    // 钩子函数
    mounted: function() {

    },
    methods: {
        sendMail: function () {
            let obj = this;
            if (obj.mobile === null || obj.mobile.trim() === '') {
                errorAlert("手机号码不能为空.");
                return false;
            }
            if (obj.name === null || obj.name.trim() === '') {
                errorAlert("姓名不能为空.");
                return false;
            }
            if (obj.email === null || obj.email.trim() === '') {
                errorAlert("邮箱地址不能为空.");
                return false;
            }
            if (obj.goal === null || obj.goal.trim() === '') {
                errorAlert("申请目标不能为空.");
                return false;
            }

            const mailReg = /^[a-zA-Z0-9_-]+@([a-zA-Z0-9]+\.)+(com|cn|net|org)$/;
            if (!mailReg.test(obj.email))  {
                errorAlert("邮箱地址错误.");
                return false;
            }

            const phoneReg = /^(((13[0-9]{1})|(15[0-9]{1})|(17[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
            if (!phoneReg.test(obj.mobile)) {
                errorAlert("手机号错误.");
                return false;
            }

            const request = {
                name: this.name,
                mobile: this.mobile,
                email: this.email,
                goal: this.goal
            }

            this.sendPost(sendEmailUrl, request).then(result => {
                if (result) {
                    obj.email = null;
                    obj.name = null;
                    obj.mobile = null;
                    obj.goal = null;
                    successAlert('发送成功.');
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
                console.log(resp);
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