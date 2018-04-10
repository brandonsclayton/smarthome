'use strict'

export default class Header {
  
  constructor(){
    this.menuItems = [
        ["Dashboard", "/smarthome"],
        ['Temperature', '/smarthome/temp.html']
    ];

    let headerD3 = d3.select("body")
        .append("header")
        .attr("class", "Header");

    let dropdownD3 = headerD3.append("div")
       .attr("class", "dropdown");

    dropdownD3.append("span")
       .attr("class", "glyphicon glyphicon-menu-hamburger dropdown-toggle")
       .attr("id", "header-menu")
       .attr("data-toggle", "dropdown");

    let headerMenuD3 = dropdownD3.append("ul")
        .attr("class", "dropdown-menu")
        .attr("aria-labelledby", "header-menu");
    
    headerMenuD3.selectAll("li")
        .data(this.menuItems)
        .enter()
        .append("li")
        .append("a")
        .text(function(d,i){ return d[0]})
        .attr("href", function(d,i){ return d[1]});

    dropdownD3.append("span")
        .attr("class", "header-title");

    headerD3.lower();
    
    this.headerEl = headerD3.node();
    this.headerListEl = this.headerEl.querySelector("ul");
    this.headerTitleEl = this.headerEl.querySelector(".header-title");
  }

  setTitle(title){
    d3.select(this.headerTitleEl)
        .text(title);
  }

}
