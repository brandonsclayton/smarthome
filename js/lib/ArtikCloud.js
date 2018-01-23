


class ArtikCloud{



  //.......................... Constructor: ArtikCloud .........................
  constructor(){
    
    let _this = this;

    _this.token = "2ad27f069c3c411a9ef84f6ca2f83b8b";
    _this.apiUrl = "https://api.artik.cloud/v1.1/"
    
    _this.wssUrl = "wss://api.artik.cloud/v1.1";
  
    let ac = new ArtikCloudAuth();
    ac.getAccessToken();
  }
  //----------------------- End Constructor: ArtikCloud ------------------------



  //...................... Method: getLastMessage ..............................
  getLastMessage(classInfo, device, count, callback){
    let _this = this;

    $.ajax({
      type: "GET",
      url: _this.apiUrl + "messages/last",
      headers: { "Authorization": "Bearer " + _this.token},
      data: { 
          "sdids": device.did,
          "count": count
      },
      success: function(response){
          callback(classInfo, response);
      },
      error: function(error){
        console.log("Error");
      }
    });

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


  postMessage(classInfo, device, data, callback){
    let _this = this;
    $.ajax({
      type: "POST",
      url: _this.apiUrl + "messages",
      headers: { "Authorization": "Bearer " + _this.token},
      data: JSON.stringify({ 
          "sdid": device.did,
          "data": data
      }),
      success: function(response){
        console.log("Test");
        console.log(response);
      },
      error: function(error){
        console.log("Error");
      }
    });


  }



}
