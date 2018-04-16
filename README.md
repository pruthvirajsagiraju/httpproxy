# httpproxy

Create a custom Proxy server implementation based on existing LittleProxy library (https://github.com/adamfisk/LittleProxy). The implementation needs to be able to accept HTTP connection from a client (on a configurable port number) and then accept messages sent by client in XML format. On the backend, the server needs to be able to connect to a configurable URL and forward those received messages to that URL. Proxy server needs to be designed in such a way that there is a place to add logic that transforms or inspects messages, however, no actual transformation or inspection logic is necessary for you to implement

Implementation 

- Implemented App.java as DefaultHttpProxyServer using little proxy library.
- Used Below Maven Dependency of LittleProxy to inject library , refer to POM.xml for more details 
       
        <dependency>
          <groupId>org.littleshoot</groupId>
          <artifactId>littleproxy</artifactId>
          <version>1.1.2</version>
        </dependency>
 - Made Port number configurable via command line arguments , this way proxy server can be brought up on any unused port , refer to below snippet in App.java
        
        int configurableportnumber = Integer.parseInt(args[0]);
        String configurableoriginserver = args[1];
        HttpFiltersSource filtersSource = getFilterSource(configurableoriginserver);
        DefaultHttpProxyServer.bootstrap()
            .withPort(configurableportnumber)
            .withFiltersSource(filtersSource)
            .withAllowRequestToOriginServer(true)
            .start();
 - Made Origin Server ( Actual Server that request needs to be forwarded ) configurable via command line arguments , refer to below snippet
              
       public HttpResponse clientToProxyRequest(HttpObject httpObject) {

        // Parsing incoming http request before fowarding.

        HttpRequest httpRequest = (HttpRequest) httpObject;
        httpRequest.setUri(configurableoriginserver);   
  
  HttpSourceFilter's clientToProxy Callback is invoked when request is first received from client, at this point made the change in request URI to origin server which can be provided as an input from comand prompt
  
  - Forwarding the request to original server is taken care by littleproxy's default implemntation which reads the host URI from httpRequest we defined above. we can override the functionality or parse the request object in multiple places refer to below snippet rom App.java
  
        @Override
        public HttpResponse proxyToServerRequest(
                HttpObject httpObject) {
                
                // we can implement the logic to parse input http request before forwarding the request XML to original server
                
            return null;
        }
  
  - Reading the Incoming XML via http POST in clientToProxyRequest, we can read chunked or full XML input using HttpPostRequestDecoder, refer to below snippet from App.java
  
        public HttpResponse clientToProxyRequest( HttpObject httpObject) {
                    	
          // Parsing incoming http XML request before fowarding.

          HttpRequest httpRequest = (HttpRequest) httpObject;
          httpRequest.setUri(configurableoriginserver);                   	
          HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(httpRequest);                    	
           List<InterfaceHttpData> postList = decoder.getBodyHttpDatas();
             for (InterfaceHttpData data : postList) {
               System.out.println(" request data "+data);
             }
            return null;
        }
 
 - Receiving the response back from original server and forward the request to client will be done using below two callbacks from littleproxy implementation. We can even intercept response object before responding back to client. Refer to below two snippets which are used for response interception
 
          public HttpObject serverToProxyResponse( HttpObject httpObject) {
              return httpObject;
          }

          public HttpObject proxyToClientResponse(HttpObject httpObject) {
              return httpObject;
          }
        
Testing 

    - Thouroughly Tested this above implementation using POSTMAN and Core JAVA Client ( Apache HttpClient ) as a client to send request to Proxy Server 
    - Original Server , implemented spring boot based rest API which accepts /post requests on port 8080 and responds back with greetings 
    - Proxy Server is brough up on port localhost:8081 to forward request to original server ( which is spring boot app running on localhost:8080 ) which is configurable i.e. we can change the original server URL via comandline arguments 






