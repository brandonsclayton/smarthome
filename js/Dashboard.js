


class Dashboard{


  //.......................... Constructor: Temperature ........................
  constructor(){

    let _this = this; 

    _this.devices = new Devices();
    _this.artikCloud = new ArtikCloud();
   
    
    _this.el = document.querySelector("#content");;


    let panelInfo = [
        ["Average Temperature", "avg-temp"],
        ["Living Room Temperature", "living-temp"],
        ["Bedroom Temperature", "bedroom-temp"]
    ];
    
    let panelsD3 = d3.select(_this.el)
        .selectAll("div")
        .data(panelInfo)
        .enter()
        .append("div")
        .attr("class", "col-sm-4 Temperature")
        .attr("id", function(d){return d[1]})
        .append("div")
        .attr("class", "panel panel-default");

    panelsD3.append("div")
        .attr("class", "panel-heading")
        .append("h3")
        .attr("class", "panel-title")
        .text(function(d){return d[0];});

    panelsD3.append("div")
        .attr("class", "panel-body");

    panelsD3.append("div")
        .attr("class", "panel-footer");

    _this.artikCloud.getLastMessage(
        _this,
        _this.devices.arduinoTemperature,
        1,
        Dashboard.setTemperaturePanel);

    /*
    _this.artikCloud.getLiveMessage(
        _this,
        _this.devices.arduinoTemperature,
        );
    */

    
    _this.avgTempEl = _this.el.querySelector("#avg-temp");
    _this.bedroomTempEl = _this.el.querySelector("#bedroom-temp");
    _this.livingTempEl = _this.el.querySelector("#living-temp");

  }
  //--------------------- End Constructor: Temperature -------------------------



  //..................... Method: setTemperaturePanel ..........................
  static setTemperaturePanel(_this, response){
    
    console.log(response);
    let metadata = response.data[0];
    let data = metadata.data;
    let ts = metadata.ts;
 
    let date = new Date(ts).toLocaleString();
   
    let temp = [
        [_this.avgTempEl, data.Average_Temperature],
        [_this.bedroomTempEl, data.Bedroom_Temperature],
        [_this.livingTempEl, data.Living_Room_Temperature]
    ];

    d3.selectAll(".Temperature")
        .select(function(d,i){return temp[i][0];})
        .select(".panel-body")
        .text(function(d,i){return temp[i][1] + "℉ "});
    
    d3.selectAll(".Temperature")
        .select(".panel-footer")
        .text(date);
    
  }
  //------------------- End Method: setTemperaturePanel ------------------------



  static printResponse(_this, response){
    console.log("Response:");
    console.log(response);

  }

}
