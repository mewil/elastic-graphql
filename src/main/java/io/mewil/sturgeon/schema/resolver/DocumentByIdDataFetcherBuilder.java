package io.mewil.sturgeon.schema.resolver;

import graphql.schema.DataFetcher;
import io.mewil.sturgeon.elasticsearch.ElasticsearchClient;
import io.mewil.sturgeon.schema.SchemaConstants;
import io.mewil.sturgeon.elasticsearch.ElasticsearchDecoder;
import io.mewil.sturgeon.schema.util.QueryFieldSelector;
import io.mewil.sturgeon.schema.util.QueryFieldSelectorResult;
import org.elasticsearch.action.get.GetResponse;

import java.util.Map;

public class DocumentByIdDataFetcherBuilder extends DataFetcherBuilder {
  public DocumentByIdDataFetcherBuilder(String index) {
    this.index = index;
  }

  private final String index;

  @Override
  public DataFetcher<Map<String, Object>> build() {
    return dataFetchingEnvironment -> {
      final QueryFieldSelectorResult selectorResult =
          QueryFieldSelector.getSelectedFieldsFromQuery(dataFetchingEnvironment);
      final String id = dataFetchingEnvironment.getArgument(SchemaConstants.ID).toString();
      final GetResponse response =
          ElasticsearchClient.getInstance().queryById(index, id, selectorResult.getFields());
      if (response.isSourceEmpty()) {
        return null;
      }
      return ElasticsearchDecoder.decodeElasticsearchDoc(
          response.getSourceAsMap(), selectorResult.getIncludeId(), response.getId());
    };
  }
}
