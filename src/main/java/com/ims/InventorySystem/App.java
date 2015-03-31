package com.ims.InventorySystem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ims.InventorySystem.AppConfiguration;
import com.ims.InventorySystem.daemon.BatchAddInventoryDaemon;
import com.ims.InventorySystem.dao.BatchAddInventoryDAO;
import com.ims.InventorySystem.dao.InventoryDAO;
import com.ims.InventorySystem.representations.InboundTransactionStatus;
import com.ims.InventorySystem.representations.Product;
import com.ims.InventorySystem.representations.SalesHistory;
import com.ims.InventorySystem.representations.TransactionCompositeId;
import com.ims.InventorySystem.resources.InventoryDetailsResource;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;


public class App extends Application<AppConfiguration>{
	
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static void main( String[] args ) throws Exception {
        new App().run(args);
    }
    
    private final HibernateBundle<AppConfiguration> hibernate = new HibernateBundle<AppConfiguration>(Product.class, SalesHistory.class, TransactionCompositeId.class, InboundTransactionStatus.class) {
		//@Override
		public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) {
			return configuration.getDataSourceFactory();
		}
	};
	
    @Override
	public void initialize(Bootstrap<AppConfiguration> bootstrap) {
		bootstrap.addBundle(hibernate);
		bootstrap.addBundle(new AssetsBundle());
	}
	
    @Override
    public void run(AppConfiguration c, Environment e) throws Exception {
    	LOG.info("Method App#run() called");
		final InventoryDAO dao = new InventoryDAO(hibernate.getSessionFactory());
		final BatchAddInventoryDAO batchAddInventoryDAO = 
				new BatchAddInventoryDAO(c.getAwsCredentials());
		ExecutorService service = Executors.newFixedThreadPool(2);
		service.execute(new BatchAddInventoryDaemon(dao, batchAddInventoryDAO));
		e.jersey().setUrlPattern("/api/*");
		e.jersey().register(new InventoryDetailsResource(dao, batchAddInventoryDAO));
    }
}
