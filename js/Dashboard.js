


class Dashboard{


  //.......................... Constructor: Temperature ........................
  constructor(){

    let _this = this; 

    _this.footer = new Footer();

    _this.header = new Header();
    _this.header.setTitle("Dashboard");

    _this.devices = new Devices();
    _this.artikCloud = new ArtikCloud();
    
    _this.el = document.querySelector("#content");;
    _this.tempStatusEl = _this.el.querySelector("#temperature-status");
    _this.avgTempEl = _this.el.querySelector("#average-temp");
    _this.livingRoomTempEl = _this.el.querySelector("#living-room-temp");
    _this.bedroomTempEl = _this.el.querySelector("#bedroom-temp");
    _this.acStatusEl = _this.el.querySelector("#ac-status");


    _this.artikCloud.getLastMessage(
        _this,
        _this.devices.arduinoTemperature.did,
        1,
        Dashboard.setTemperaturePanel);

    
    _this.artikCloud.getLastMessage(
        _this,
        _this.devices.harmonyAC.did,
        1,
        Dashboard.setACPanel);
    
    /*
    _this.artikCloud.getLiveMessage(
        _this,
        _this.devices.arduinoTemperature,
        );
    */
    
    
    /*
    _this.artikCloud.postMessage(_this, 
        _this.devices.arduinoTemperature,
        {"averageTemperature":73.52,
            "bedroomTemperature":74.75,
            "livingRoomTemperature":72.28}, 
        Dashboard.printResponse);
    */

  }
  //--------------------- End Constructor: Temperature -------------------------



  //..................... Method: setTemperaturePanel ..........................
  static setACPanel(_this, response){
    console.log(response);
    let metadata = response.data[0];
    let data = metadata.data;
    let ts = metadata.ts;
 
    let date = new Date(ts).toLocaleString();
    
    let state = data.state.toUpperCase(); 


    d3.select(_this.acStatusEl)
        .classed("hidden", false)
        .select(".panel-body")
        .text(state)
    
    d3.select(_this.acStatusEl)
        .select(".panel-footer")
        .text(date);
    
  }
  //------------------- End Method: setTemperaturePanel ------------------------
  
  
  
  //..................... Method: setTemperaturePanel ..........................
  static setTemperaturePanel(_this, response){
    
    let metadata = response.data[0];
    let data = metadata.data;
    let ts = metadata.ts;
 
    let date = new Date(ts).toLocaleString();
   
    let temp = [
        [_this.avgTempEl, data.Average_Temperature],
        [_this.bedroomTempEl, data.Bedroom_Temperature],
        [_this.livingRoomTempEl, data.Living_Room_Temperature]
    ];

    d3.select(_this.tempStatusEl)
        .classed("hidden", false)
        .selectAll(".temp")
        .select(function(d,i){return temp[i][0];})
        .text(function(d,i){return temp[i][1] + "℉ "});
    
    d3.select(_this.tempStatusEl)
        .select(".panel-footer")
        .text(date);
    
  }
  //------------------- End Method: setTemperaturePanel ------------------------



  static printResponse(_this, response){
    console.log("Response:");
    console.log(response);

  }

}
