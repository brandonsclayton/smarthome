'use static'

export default class ArtikCloud{
  
  constructor(){
    this.oneDayInMilliSec = 1*24*60*60*1000;
    this.apiUrl = "https://api.artik.cloud/v1.1"
    this.wssUrl = "wss://api.artik.cloud/v1.1";
    this.authUrl = "https://accounts.artik.cloud";
    this.clientId = "cbdf047c17a14002830333c0906f1bba";
    this.clientSecret = "9d4bb87414a64b50a321c3c8bd5c640c";
    this.checkToken();
  }

  /**
  * getMessage
  */
  getMessage(deviceId, startDate, endDate, callback) {
    console.log(startDate);
    console.log(new Date(startDate).toLocaleTimeString() + " " +
        new Date(startDate).toLocaleDateString());
    console.log(endDate);
    console.log(new Date(endDate).toLocaleTimeString() + " " + 
        new Date(endDate).toLocaleDateString());

    let type = "GET";
    let headerParams = {"Authorization" : "Bearer " + this.token};
    let queryParams = {
      "sdid": deviceId,
      "startDate": startDate,
      "endDate": endDate,
      "count": 500,
    };
    let url = this.apiUrl + "/messages";

    this.request(
      url,
      type,
      queryParams,
      headerParams,
      callback);
  }

  /**
  * getLastMessage
  */
  getLastMessage(deviceIds, count, callback){
    let type = "GET";
    let headerParams = { "Authorization": "Bearer " + this.token};
    let queryParams = {
        "sdids": deviceIds,
        "count": count
    };
    let url = this.apiUrl + "/messages/last";

    this.request(
        url, 
        type, 
        queryParams, 
        headerParams, 
        callback);   
  }
  
  /**
  * getLiveMessage
  */
  getLiveMessage(deviceIds, callback){
    let url = this.wssUrl + "/live" +
        "?sdids=" + deviceIds +
        "&Authorization=Bearer " + this.token;
    let webSocket = new WebSocket(url);
    
    webSocket.onmessage = (response) => {
      let message = JSON.parse(response.data);
      if (message.mid != undefined){
        this.liveCallback = callback;
        this.liveCallback(message, true);
      }
    }
  }

  /**
  * postMessage
  */
  postMessage(deviceId, data, callback){
    let type = "POST";
    let url = this.apiUrl + "/messages";
    let headerParams = { "Authorization": "Bearer " + this.token};
    let queryParams = JSON.stringify({ 
          "sdid": deviceId,
          "data": data
      });
    
    this.request(
        url, 
        type, 
        queryParams, 
        headerParams, 
        callback);
  }
  
  /**
  * login
  */
  login(){
    let url = this.authUrl + 
        "/authorize" +
        "?prompt=login" +
        "&client_id=" + this.clientId +
        "&response_type=token" +
        "&account_type=GOOGLE";
    
    window.location = url;
  }

  /**
  * checkToken
  */
  checkToken(){
    let token = localStorage.getItem("token");
    let tokenExpiresOn = parseFloat(localStorage.getItem("expiresOn"));
    let dateCheck = new Date().getTime();
    
    let url = window.location.hash.substring(1);
    let includesToken = url.includes("access_token"); 
   
    if(includesToken){
      console.log("Getting token from URL");
      this.getAccessToken(url);
    }
    else if (token != null && 
        (tokenExpiresOn - this.oneDayInMilliSec > dateCheck)) {
      console.log("Getting token from local storage");
      this.token = token;
      this.tokenExipresOn = tokenExpiresOn;
    }else{
      console.log("Getting new token");
      this.newToken();
    }
  }

  /**
  * getAccessToken
  */
  getAccessToken(url){
    
    let pars = url.split("&");
    window.location.hash = '';
    let key, 
        code,
        value;
    pars.forEach((par,i) => {
      key = par.split("=")[0];
      value = par.split("=")[1];
      if (key == "access_token"){
        this.token = value;
      }else if (key == "expires_in"){
        let ts = new Date().getTime() + value * 1000;
        this.expiresOn = ts;
      }
    });
    
    this.setToken();
  }

  /**
  * setToken
  */
  setToken(){
    localStorage.setItem("token", this.token);
    localStorage.setItem("expiresOn", this.expiresOn);
  }

  /**
  * newToken
  */
  newToken(){
    
    let modalD3 = d3.select("body")
        .append("div")
        .attr("class", "modal fade Login")
        .attr("id", "login-dialog")
        .attr("role", "dialog");

    let contentD3 = modalD3.append("div")
        .attr("class", "modal-dialog")
        .append("div")
        .attr("class", "modal-content");
  
    let headerD3 = contentD3.append("div")
        .attr("class", "modal-header");
    let titleD3 = headerD3.append("h4")
        .attr("class", "modal-title");

    titleD3.append("span")
        .attr("class", "glyphicon glyphicon-home");
    titleD3.append("span")
        .text("The Clayton Smarthome");
    titleD3.append("span")
        .attr("class", "glyphicon glyphicon-home");
   
    let bodyD3 = contentD3.append("div")
        .attr("class", "modal-body");
    let loginD3 = bodyD3.append("button")
        .attr("class", "btn btn-primary")
        .attr("type", "button")
        .text("Login in with Google");

    modalD3.lower();
    $(modalD3.node()).modal({
        show: true,
        keyboard: false,
        backdrop: "static"
    });
     
    modalD3.on("click", () => {
    });
    
    contentD3.on("click", () => {
      this.login();
    });

  }

  request(
      url,
      type,
      queryParams,
      headerParams,
      callback){
    $.ajax({
        type: type,
        url: url,
        headers: headerParams,
        data: queryParams,
        success: (response) => {
          try{
            this.requestCallback = callback;
            this.requestCallback(response);
          }catch(err){
          }
        },
        error: (error) => {
          console.log("Request Class Error:");
          console.log(error);
        }
    });
  }

  pastHours(hour) {
    let currentTime = Date.now();
    return (currentTime - this.oneDayInMilliSec * hour / 24);
  }

}
