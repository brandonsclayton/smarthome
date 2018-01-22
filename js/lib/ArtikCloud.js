


class ArtikCloud{



  //.......................... Constructor: ArtikCloud .........................
  constructor(){
    
    let _this = this;

    _this.token = "2ad27f069c3c411a9ef84f6ca2f83b8b";
    _this.apiUrl = "https://api.artik.cloud/v1.1/"
    
  }
  //----------------------- End Constructor: ArtikCloud ------------------------



  //...................... Method: getLastMessage ..............................
  getLastMessage(classInfo, device, count, callback){
    let _this = this;

    $.ajax({
      type: "GET",
      url: _this.apiUrl + "messages/last",
      headers: { "Authorization": "Bearer " +_this.token},
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
  


}
