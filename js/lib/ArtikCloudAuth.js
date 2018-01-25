

class ArtikCloudAuth{


  //............................ Constructor ...................................
  constructor(){

    let _this = this;

    _this.authUrl = "https://accounts.artik.cloud";
    _this.clientId = "cbdf047c17a14002830333c0906f1bba";
    _this.clientSecret = "9d4bb87414a64b50a321c3c8bd5c640c";
    _this.redirectUrl = "http://localhost:8080/smarthome/main.html";
    
  }
  //---------------------------- End Constructor -------------------------------



  //............................. Method: login ................................
  login(){
    let _this = this;
    _this.loginEl = document.querySelector("#login-panel");
    let url = _this.authUrl + 
        "/authorize" +
        "?prompt=login" +
        "&client_id=" + _this.clientId +
        "&response_type=token" +
        "&account_type=GOOGLE" +
        "&redirect_uri=" + _this.redirectUrl;
    
    _this.loginEl.onclick = function(){
      window.location = url;
    };  
  }
  //------------------------- End Method: login --------------------------------



  //............................. Method: checkToken ........................... 
  static checkToken(){
    
    let token = localStorage.getItem("token");
    return token;
  }
  //--------------------------- End Method: checkToken -------------------------



  //........................... Method: getAccesToken ..........................
  getAccessToken(){
    let _this = this;
    
    let tokenCheck = ArtikCloudAuth.checkToken();
    if (tokenCheck != null) return tokenCheck;
    
    let url = window.location.hash.substring(1);
    let pars = url.split("&");
    let key, 
        code,
        value;
    pars.forEach(function(par,i){
      key = par.split("=")[0];
      value = par.split("=")[1];
      if (key == "access_token"){
        _this.token = value;
        localStorage.setItem("token", _this.token); 
        return value;
      }
    });
    
  }
  //--------------------- End Method: getAccessToken ---------------------------



}
//----------------------- End Class: ArtikCloudAuth ----------------------------
