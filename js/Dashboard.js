


class Dashboard extends ArtikCloud{


  constructor(){
    super();
    this.footer = new Footer();

    this.header = new Header();
    this.header.setTitle("Dashboard");
    this.devices = new Devices();
   
    this.el = document.querySelector("#content");;
    this.tempStatusEl = this.el.querySelector("#temperature-status");
    this.avgTempEl = this.el.querySelector("#average-temp");
    this.livingRoomTempEl = this.el.querySelector("#living-room-temp");
    this.bedroomTempEl = this.el.querySelector("#bedroom-temp");
    this.acStatusEl = this.el.querySelector("#ac-status");
    this.acPanelBody = this.acStatusEl.querySelector(".panel-body");
    this.tempOuterEl = this.tempStatusEl.querySelector(".outer-panel");

    if (this.token != null || this.token != undefined){
      this.getData();
    }
  }

  getData(){
    
    this.getLastMessage(
        this.devices.arduinoTemperature.did,
        1,
        this.setTemperaturePanel);
    
    this.getLastMessage(
        this.devices.harmonyAC.did,
        1,
        this.setACPanel);
  
    
    this.getLiveMessage(
        this.devices.arduinoTemperature.did,
        this.setTemperaturePanel);
    
    this.getLiveMessage(
        this.devices.harmonyAC.did,
        this.setACPanel);
  }

  //..................... Method: setTemperaturePanel ..........................
  setACPanel(_this, response, isLive = false){
    let data;
    let ts;
    if (isLive){
      data = response.data;
      ts = response.ts;
    }else{
      let metadata = response.data[0];
      data = metadata.data;
      ts = metadata.ts;
    }
 
    let date = new Date(ts).toLocaleDateString();
    let time = new Date(ts).toLocaleTimeString();
    let state = data.state.toUpperCase(); 

    d3.select(_this.acStatusEl)
        .classed("hidden", false)
        .select(".panel-body")
        .text(state)
    
    d3.select(_this.acStatusEl)
        .select(".panel-footer")
        .text("Last Updated: " + time + " on " + date);

  }
  //------------------- End Method: setTemperaturePanel ------------------------
  
  
  
  //..................... Method: setTemperaturePanel ..........................
  setTemperaturePanel(_this, response, isLive = false){
    let data;
    let ts;
    if (isLive){
      data = response.data;
      ts = response.ts;
    }else{
      let metadata = response.data[0];
      data = metadata.data;
      ts = metadata.ts;
    }
    
    let date = new Date(ts).toLocaleDateString();
    let time = new Date(ts).toLocaleTimeString();
   
    let temp = [
      [_this.avgTempEl, data.Average_Temperature],
      [_this.bedroomTempEl, data.Bedroom_Temperature],
      [_this.livingRoomTempEl, data.Living_Room_Temperature]
    ];

    d3.select(_this.tempStatusEl)
        .classed("hidden", false)
        .selectAll(".temp")
        .select(function(d,i){return temp[i][0];})
        .html(function(d,i){
          return temp[i][1] + "℉ "; 
        });
    
    d3.select(_this.tempStatusEl)
        .select(".panel-footer")
        .text("Last Updated: " + time + " on " + date);
    
    let height = _this.tempOuterEl.clientHeight;
    d3.select(_this.acPanelBody)
        .style("height", height + "px");
  }
  //------------------- End Method: setTemperaturePanel ------------------------



  printResponse(response){
    console.log("Response:");
    console.log(response);

  }

}
