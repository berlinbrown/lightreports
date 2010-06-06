
import java.util.ArrayList
import java.util.List
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.cookie.Cookie
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.HTTP
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.client.ResponseHandler

object Client {
  def main(args: Array[String]) {
	println("Running")
	val httpclient = new DefaultHttpClient()
	val httpget = new HttpGet("http://www.google.com")
    //val handler:ResponseHandler[String] = new BasicResponseHandler()
	val handler:ResponseHandler[String] = new BasicResponseHandler()
    val response = httpclient.execute[String](httpget, handler)
    println(response)
	println("Done")
  }
}
// End of File
