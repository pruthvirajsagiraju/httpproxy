package sample.httproxy;

import java.net.InetSocketAddress;
import java.util.List;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSource;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
/**
 * Http Proxy Implementation
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	
    	int configurableportnumber = Integer.parseInt(args[0]);
    	
    	String configurableoriginserver = args[1];
        
    	HttpFiltersSource filtersSource = getFilterSource(configurableoriginserver);
    	
        DefaultHttpProxyServer.bootstrap()
                .withPort(configurableportnumber)
                .withFiltersSource(filtersSource)
                .withAllowRequestToOriginServer(true)
                .start();
    }
    
    private static HttpFiltersSourceAdapter getFilterSource(final String configurableoriginserver) {
    	return new HttpFiltersSourceAdapter() {
            public HttpFilters filterRequest(HttpRequest originalRequest) {

                return new HttpFiltersAdapter(originalRequest) {
                    @Override
                    public HttpResponse clientToProxyRequest(
                            HttpObject httpObject) {
                    	
                    	// Parsing incoming http request before fowarding.
                    	  
                    	HttpRequest httpRequest = (HttpRequest) httpObject;
                    	httpRequest.setUri(configurableoriginserver);                   	
                    	HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(httpRequest);                    	
                    	 List<InterfaceHttpData> postList = decoder.getBodyHttpDatas();
                         for (InterfaceHttpData data : postList) {
                        	 System.out.println(" request data "+data);
                         }
                        return null;
                    }
                    
                   

                    @Override
                    public HttpResponse proxyToServerRequest(
                            HttpObject httpObject) {
                        return null;
                    }

                    @Override
                    public void proxyToServerRequestSending() {
                        //proxyToServerRequestSendingNanos.set(requestCount.get(), now());
                    }

                    @Override
                    public void proxyToServerRequestSent() {
                        //proxyToServerRequestSentNanos.set(requestCount.get(), now());
                    }

                    public HttpObject serverToProxyResponse(
                            HttpObject httpObject) {
                        return httpObject;
                    }

                    public HttpObject proxyToClientResponse(
                            HttpObject httpObject) {
                        return httpObject;
                    }

                    @Override
                    public void proxyToServerConnectionQueued() {
                        
                    }

                    @Override
                    public InetSocketAddress proxyToServerResolutionStarted(
                            String resolvingServerHostAndPort) {
                       
                        return super.proxyToServerResolutionStarted(resolvingServerHostAndPort);
                    }

                    @Override
                    public void proxyToServerResolutionFailed(String hostAndPort) {
         
                    }

                    @Override
                    public void proxyToServerResolutionSucceeded(
                            String serverHostAndPort,
                            InetSocketAddress resolvedRemoteAddress) {
                    
                    }

                    @Override
                    public void proxyToServerConnectionStarted() {
                      
                    }

                    @Override
                    public void proxyToServerConnectionSSLHandshakeStarted() {
                    
                    }

                    @Override
                    public void proxyToServerConnectionFailed() {
                      
                    }

                    @Override
                    public void proxyToServerConnectionSucceeded(ChannelHandlerContext ctx) {
                        
                    }
                    
                    private long now() {
                        return System.nanoTime();
                    }
                    

                };
            }

            public int getMaximumRequestBufferSizeInBytes() {
                return 1024 * 1024;
            }

            public int getMaximumResponseBufferSizeInBytes() {
                return 1024 * 1024;
            }
        };

        }
    }