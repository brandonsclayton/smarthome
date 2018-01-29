"use strict"

class ArtikCloudAuth{


  //............................ Constructor ...................................
  constructor(){

    let _this = this;

    _this.authUrl = "https://accounts.artik.cloud";
    _this.clientId = "cbdf047c17a14002830333c0906f1bba";
    _this.clientSecret = "9d4bb87414a64b50a321c3c8bd5c640c";
    _this.redirectUrl = "http://localhost:8080/smarthome/";
    
  }
  //---------------------------- End Constructor -------------------------------



  //............................. Method: login ................................
  static login(_this){
    
    let url = _this.authUrl + 
        "/authorize" +
        "?prompt=login" +
        "&client_id=" + _this.clientId +
        "&response_type=token" +
        "&account_type=GOOGLE" +
        "&redirect_uri=" + _this.redirectUrl;
    
    window.location = url;
  }
  //------------------------- End Method: login --------------------------------



  //............................. Method: checkToken ........................... 
  checkToken(){
    let _this = this; 
    let token = localStorage.getItem("token");
    let tokenExpiresOn = parseFloat(localStorage.getItem("expiresOn"));

    let dateCheck = new Date().getTime();
    let oneDay = 1*24*60*60*1000;
    
    let url = window.location.hash.substring(1);
    let includesToken = url.includes("access_token"); 
    
    if( (token == null || isNaN(tokenExpiresOn)) && includesToken){
      console.log("Getting token from URL");
      ArtikCloudAuth.getAccessToken(_this, url);
    }
    else if (tokenExpiresOn - oneDay > dateCheck){
      console.log("Getting token from local storage");
      _this.token = token;
      _this.tokenExipresOn = tokenExpiresOn;
    }else{
      console.log("Getting new token");
      ArtikCloudAuth.newToken(_this);
    }
  
  }
  //--------------------------- End Method: checkToken -------------------------



  //........................... Method: getAccesToken ..........................
  static getAccessToken(_this, url){
    
    let pars = url.split("&");
    let key, 
        code,
        value;
    pars.forEach(function(par,i){
      key = par.split("=")[0];
      value = par.split("=")[1];
      if (key == "access_token"){
        _this.token = value;
      }else if (key == "expires_in"){
        let ts = new Date().getTime() + value * 1000;
        _this.expiresOn = ts;
      }
    });
    
    ArtikCloudAuth.setToken(_this);
  }
  //--------------------- End Method: getAccessToken ---------------------------


  //......................... Method: setToken .................................
  static setToken(_this){
    localStorage.setItem("token", _this.token);
    localStorage.setItem("expiresOn", _this.expiresOn);
  }
  //------------------------ Method: setToken ----------------------------------



  //....................... Method: newToken ...................................
  static newToken(_this){
    
    let modalD3 = d3.select("body")
        .append("div")
        .attr("class", "modal fade Login")
        .attr("id", "login-dialog")
        .attr("role", "dialog");

    let contentD3 = modalD3.append("div")
        .attr("class", "modal-dialog modal-sm")
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
     
    modalD3.on("click", function(){
    });
    
    contentD3.on("click", function(){
      ArtikCloudAuth.login(_this);
    });

  }
  //----------------------- End Method: newToken -------------------------------

}
//----------------------- End Class: ArtikCloudAuth ----------------------------
