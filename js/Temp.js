'use strict'

import ArtikCloud from './lib/ArtikCloud.js';
import Devices from './lib/Devices.js';
import Footer from './lib/Footer.js';
import Header from './lib/Header.js';
import D3LinePlot from './lib/D3LinePlot.js';

export default class Temp extends ArtikCloud{

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
      legendLocation: 'bottomleft',
      tooltipText: ["Room", "Date", "Temperature"],
    };
    let acOptions = {};

    this.plot = new D3LinePlot(
        this.el,
        plotOptions,
        tempOptions,
        acOptions);
  }


  plotTemp(response, isLive = false){
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

    this.plot.title = "Temperature";
    this.plot.upperPanel.data = seriesData;
    this.plot.upperPanel.dataTableTitle = "Temperature";
    this.plot.upperPanel.labels = seriesLabels;
    this.plot.upperPanel.metadata = {
      url: window.location, 
      date: new Date()
    };
    this.plot.upperPanel.ids = seriesIds;
    this.plot.upperPanel.xLabel = "Date";
    this.plot.upperPanel.yLabel = "Temperature"
    
    this.plot.plotData(this.plot.upperPanel, null , [65, 80]);
    
  }


}
