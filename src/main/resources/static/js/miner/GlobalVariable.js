let isAuth = false;
let userCode;

// server url
// let serverUrl = "http://127.0.0.1:8999";
let serverUrl = "http://103.45.102.252:8999";

// socket url
// let wsServer = 'ws://127.0.0.1:8990';
let wsServer = 'ws://103.45.102.252:8990';

// api url
let userAuthUrl = '/api/v1/user/auth';
let sendEmailUrl = '/api/v1/send/email';

// 返回码
const SUCCESS = 1;
const FAILED = 2;
const ERROR_PARAMS = 3;
const INNER_ERROR = 4;

// socket code
const NOT_AUTH = 2000;
const IS_AUTH = 2001;

const USER_PROFILE = 3000;