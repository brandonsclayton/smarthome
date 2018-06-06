'use strict'

import Devices from './lib/Devices.js';
import Footer from './lib/Footer.js';
import Header from './lib/Header.js';

export default class Dashboard {

  constructor() {
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
   
    let protocol = document.location.protocol;
    let hostname = document.location.hostname;
    let host = document.location.host;

    this.baseUrl = hostname == "localhost" ? 
        host + '/smarthome-ws' :
        host;

    this.webServiceUrl = protocol + '//' + this.baseUrl + 
        '/getLastMessage?devicegroup=temperature&count=1';
   
    this.webSocketUrl = 'ws://' + this.baseUrl + '/getLiveMessage';

    this.getData();
  }

  getData() {
    let promise = $.getJSON(this.webServiceUrl);
    promise.done((result) => {
      this.setTemperaturePanel(result);
      this.setACPanel(result);
    });
   
    this.getLiveMessage(this.devices.temperature.id, this.setTemperaturePanel);
    this.getLiveMessage(this.devices.ac.id, this.setACPanel);
  }
  
  setACPanel(result) {
    let response = result.response.find((response) => {
      return response.device == 'AC';
    });

    d3.select(this.acStatusEl)
        .classed('hidden', false)
        .select('.panel-body')
        .text(response.dataGroup[0].data[0].toUpperCase());

    d3.select(this.acStatusEl)
        .select('.panel-footer')
        .text('Last Updated: ' + response.date[0]);
  }

  setTemperaturePanel(result) {
    let response = result.response.find((response) => {
      return response.device == 'TEMPERATURE';
    });

    d3.select(this.tempStatusEl)
        .classed('hidden', false);

    for (let d of response.dataGroup) {
      d3.select(this.tempStatusEl)
          .select('#' + d.id)
          .html(d.data[0] + '℉ '); 
    }

    d3.select(this.tempStatusEl)
        .select('.panel-footer')
        .text('Last Updated: ' + response.date[0]);
  }

  getLiveMessage(device, callback) {
    let url = this.webSocketUrl + '?device=' + device;
    let ws = new WebSocket(url);

    ws.onopen = () => {
      console.log('Connected');
    };

    ws.onclose = () => {
      console.log('Closed');
    }

    ws.onerror = () => {
      console.log('Websockets Error')
    };

    ws.onmessage = (response) => {
      let data = JSON.parse(response.data);
      let status = data.status;

      if (status == "Success") {
        console.log('Web Sockets On Message:');
        console.log(data);
        this._liveCallback = callback;
        this._liveCallback(data);
      }
    };
  }

}
