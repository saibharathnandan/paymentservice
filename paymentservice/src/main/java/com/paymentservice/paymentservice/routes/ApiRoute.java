package com.paymentservice.paymentservice.routes;

import com.pps.model.Payment;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApiRoute  {

    @Autowired
    CamelContext context;

    public void startRoutes() throws Exception {

        RouteBuilder routeBuilder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                restConfiguration()
                        .component("servlet")
                        .bindingMode(RestBindingMode.json);

                getContext().getGlobalOptions().put("CamelJacksonEnableTypeConverter","true");
                getContext().getGlobalOptions().put("CamelJacksonTypeConverterToPojo","true");

                rest("/api")
                        .id("api-route")
                        .consumes("application/json")
                        .post("/fraudCheck")
                        .type(Payment.class)
                        .to("direct:validateRequest");

                from("direct:validateRequest")
                        .to("bean-validator://x")
                        .convertBodyTo(String.class)
                        .to("http://localhost:8081/payments/api/brokerService");

            }
        };
        context.addRoutes(routeBuilder);

    }

}
