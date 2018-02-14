'use strict'


export default class Devices{

  constructor(){
    this.arduinoTemperature = {
        label: "Arduino Temperature",
        did: "1497d25089db4a8d84997fd5b2a3d65f",
        dtid: "dt94cd81097d7f48b3a208a014ca5c1db7",
    };

    this.harmonyAC = {
        label: "Air Conditioner",
        did: "22190f84bb0845e5a571ab17269f88a4",
        dtid: "dt26557eba716b4058931840791ac039b0"
    };

    this.bedroomLights = [
        { 
            label: "Bedroom Ceiling Fan 1",
            did: "30ad6da22dd94bd896b90c9f61a7c7e8",
            dtid: "dt6f79b9b4aa3b4a80b7b76c2190016c61"
        },{
            label: "Bedroom Ceiling Fan 2",
            did: "4c507fb2595f4e6f854b976ffcd7c201",
            dtid: "dt6f79b9b4aa3b4a80b7b76c2190016c61"
        },{
            label: "Bedroom Ceiling Fan 3",
            did: "dbc02b93b20c4f5b98b6344d95f57f36",
            dtid: "dt6f79b9b4aa3b4a80b7b76c2190016c61"
        },{
            label: "Bedroom Ceiling Fan 4",
            did: "2ae2d82547db42e2ba8cc68eacae3a19",
            dtid: "dt6f79b9b4aa3b4a80b7b76c2190016c61"
        }
    ];
  
  
  }

}
