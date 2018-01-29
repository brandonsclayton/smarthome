



class Header{

  
  constructor(){
    let _this = this;

    _this.menuItems = [
        ["Dashboard", "/smarthome/main.html"],
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
        .data(_this.menuItems)
        .enter()
        .append("li")
        .append("a")
        .text(function(d,i){ return d[0]})
        .attr("href", function(d,i){ return d[1]});

    dropdownD3.append("span")
        .attr("class", "header-title");

    headerD3.lower();
    
    _this.headerEl = headerD3.node();
    _this.headerListEl = _this.headerEl.querySelector("ul");
    _this.headerTitleEl = _this.headerEl.querySelector(".header-title");
  }


  setTitle(title){
    let _this = this;

    d3.select(_this.headerTitleEl)
        .text(title);
  }

}
