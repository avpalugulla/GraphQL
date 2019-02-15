package com.ashok.graphql.GraphQL;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component
public class GraphQLProvider {
    
    
    
    // The GraphQL Java Spring adapter will use that GraphQL instance to make our schema available via HTTP on the default url /graphql.
    private GraphQL graphQL;
    
    
    @Autowired
    GraphQLDataFetchers graphQLDataFetchers;
    
    
    @Bean
    public GraphQL graphQL(){
        return this.graphQL;
    }


    @PostConstruct
    public void init() throws IOException {
        URL url = Resources.getResource("schema.graphqls"); // schema file in src/resources
        String sdl = Resources.toString(url, Charsets.UTF_8);   // contains the contents of schema file.
        System.out.println("Schema File : "+sdl);
        
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    
    }
    
    private GraphQLSchema buildSchema(String sdl) {
    
        TypeDefinitionRegistry typesRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring wiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typesRegistry, wiring);
        
    }
    
    private RuntimeWiring buildWiring() {
    
        return RuntimeWiring.newRuntimeWiring().type(newTypeWiring("Query") .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher()))
                .type(newTypeWiring("Book").dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher()))
                .build();
    }
    
    
}
