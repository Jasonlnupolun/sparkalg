package es;

import java.sql.SQLFeatureNotSupportedException;

import org.apache.log4j.chainsaw.Main;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.exception.SqlParseException;

public class EsClient {

	
	SearchDao searchDao;
	public EsClient(){
	Client client = null;//new TransportClient().addTransportAddress(new InetSocketTransportAddress("61.129.70.43", 9300));
    searchDao = new SearchDao(client);
	}
	
	public SearchHits queryBySql(String sql ) throws SQLFeatureNotSupportedException, SqlParseException{
		SearchRequestBuilder select  = (SearchRequestBuilder)searchDao.explain(sql);
		SearchHits hits = select.get().getHits();
		return hits;
	}
	
	public static void main(String[] args) throws SQLFeatureNotSupportedException, SqlParseException {
		new EsClient().queryBySql("select * from lsyindex where type = 'M' limit 1000");
	}
}
