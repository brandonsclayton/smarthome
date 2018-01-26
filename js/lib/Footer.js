




class Footer{

  constructor(){
    let _this = this;

    let footerD3 = d3.select("body")
        .append("footer")
        .attr("class", "Footer");

    footerD3.append("span")
        .attr("class", "glyphicon glyphicon-cog settings");
   
    footerD3.lower();

    _this.footerEl = footerD3.node();
    _this.settingEl = _this.footerEl.querSelector(".settings");

  }



}
