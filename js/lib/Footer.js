'use strict'

export default class Footer {

  constructor() {
    let footerD3 = d3.select("body")
        .append("footer")
        .attr("class", "Footer");

    footerD3.append("span")
        .attr("class", "glyphicon glyphicon-cog settings");
   
    footerD3.lower();

    this.footerEl = footerD3.node();
    this.settingEl = this.footerEl.querySelector(".settings");

  }

}
