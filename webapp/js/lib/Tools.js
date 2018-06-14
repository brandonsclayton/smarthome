
export default class Tools {

  static getJSON(url) {
    let jsonReject;

    let promise = new Promise((resolve, reject) => {
      jsonReject = reject;

      let jsonCall = $.getJSON(url);
      jsonCall.done(resolve);

      let message = 'Could not reach: ' + url;
      jsonCall.fail(() => { reject(message); });
    });
    
    return {promise: promise, reject: jsonReject};
  }

  static getJSONs(urls) {
    let promises = [];
    let jsonRejects = [];

    for (let url of urls) {
      let jsonCall = Tools.getJSON(url);
      promises.push(jsonCall.promise);
      jsonRejects.push(jsonCall.reject);
    }

    return {promises: promises, rejects: jsonRejects, reject: jsonRejects[0]};
  }

}