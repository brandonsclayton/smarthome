


class ArtikCloud{



  //.......................... Constructor: ArtikCloud .........................
  constructor(){
    
    let _this = this;

    _this.apiUrl = "https://api.artik.cloud/v1.1"
    
    _this.wssUrl = "wss://api.artik.cloud/v1.1";
  
  }
  //----------------------- End Constructor: ArtikCloud ------------------------



  //...................... Method: getLastMessage ..............................
  getLastMessage(classInfo, deviceIds, count, callback){
    let _this = this;

    let type = "GET";
    let headerParams = { "Authorization": "Bearer " + _this.token};
    let queryParams = {
        "sdids": deviceIds,
        "count": count
    };
    let url = _this.apiUrl + "/messages/last";

    Request.request(classInfo, url, type, 
        queryParams, headerParams, callback);   
    
  }
  //-------------------- End Method: getLastMessage ----------------------------
  


  //...................... Method: getLiveMessage ..............................
  getLiveMessage(classInfo, device, callback){
    let _this = this;

    let url = _this.wssUrl + "/live" +
        "?sdids=" + device.did +
        "&Authorization=Bearer " + _this.token;
    let webSocket = new WebSocket(url);
    
    webSocket.onmessage = function(event){
      console.log("Message");
      console.log(event);
    }
    
  }
  //-------------------- End Method: getLastMessage ----------------------------


  //.................... Method: postMessage ...................................
  postMessage(classInfo, deviceId, data, callback){
    let _this = this;
    let type = "POST";
    let url =_this.apiUrl + "/messages";
    let headerParams = { "Authorization": "Bearer " + _this.token};
    let queryParams = JSON.stringify({ 
          "sdid": deviceId,
          "data": data
      });
    
    Request.request(classInfo, url, type, 
        queryParams, headerParams, callback);
  }
  //-------------------- End Method: postMessage -------------------------------


  



}
