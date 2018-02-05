


class Temp extends ArtikCloud{

  constructor(){
    super();
    
    this.footer = new Footer();
    this.header = new Header();
    this.header.setTitle("Temperature");
    this.devices = new Devices();
   
    this.el = document.querySelector("#content");;
    this.plotSetup();
    if (this.token != null || this.token != undefined){
      this.getData();
    }
  }

  /*
  * getData
  */
  getData(){
    
    this.getMessage(
        this.devices.arduinoTemperature.did,
        this.pastHours(1),
        Date.now(),
        this.plotTemp);
    
    /*
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
    */
  }

  
  plotSetup(){
    let plotOptions = {
      xAxisScale: "linearTime",
      yAxisScale: "linear",
    };
    let tempOptions = {
      tooltipText: ["Room", "Date", "Temperature"],
    };
    let acOptions = {};

    this.plot = new D3LinePlot(
        this.el,
        plotOptions,
        tempOptions,
        acOptions);
  }


  plotTemp(_this, response, isLive = false){
    let data = response.data;
    let avgData = [];
    let bedroomData = [];
    let livingData = [];
    let ts = [];
    data.forEach((d,i) => {
      avgData.push(d.data.Average_Temperature);
      bedroomData.push(d.data.Bedroom_Temperature);
      livingData.push(d.data.Living_Room_Temperature);
      ts.push(d.ts);
    });
    
    let seriesData = [];
    let seriesLabels = ["Average", "Bedroom", "Living Room"];
    let seriesIds = ["avg", "bed", "living"];
    
    seriesData.push(d3.zip(ts, avgData));
    seriesData.push(d3.zip(ts, bedroomData));
    seriesData.push(d3.zip(ts, livingData));

    _this.plot.title = "Temperature";
    _this.plot.upperPanel.data = seriesData;
    _this.plot.upperPanel.dataTableTitle = "Temperature";
    _this.plot.upperPanel.labels = seriesLabels;
    _this.plot.upperPanel.metadata = {
      url: window.location, 
      date: new Date()
    };
    _this.plot.upperPanel.ids = seriesIds;
    _this.plot.upperPanel.xLabel = "Date";
    _this.plot.upperPanel.yLabel = "Temperature"
    
    _this.plot.plotData(_this.plot.upperPanel);
    
    d3.select(_this.plot.upperPanel.svgEl)
        .call(d3.zoom().on("zoom", () => {
        }));
  }


}
