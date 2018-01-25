



class Request{

  static request(classInfo,
      url, 
      type,
      queryParams,
      headerParams,
      callback){
    
    
    $.ajax({
        type: type,
        url: url,
        headers: headerParams,
        data: queryParams,
        success: function(response){
          try{
            callback(classInfo, response);
          }catch(err){
          }
        },
        error: function(error){
          console.log("Request Class Error:");
          console.log(error);
        }
    });


  }




}
