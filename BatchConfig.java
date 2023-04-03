import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    public FlatFileItemReader<user> reader() {
        FlatFileItemReader<user> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("Data.csv"));
        raeder.setLineMapper(getLineMapper());
        reader.setLinesToSkip(1);
        return reader();

    }

    private LineMapper<user> getLineMapper() {
        DefultLineMapper<user> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[]{"endYear", "citylng", "citylat", "intensity", "sector", "topic", "insight", "swot", "url", "region", "startYear", "impact", "added", "published", "city", "country", "relevance", "pestle", "source", "title", "liklihood"});
        lineTokenizer.setIncludedFields(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22});

        BeanWrapperFieldSetMapper<user> fildSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(user.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fildSetMapper);

        return LineMapper;
    }

    @Bean
    public UserItemProcess process() {
        return new UserItemProcess();
    }

    public JdbcBatchItemWriter<user> writer() {

        JdbcBatchItemWriter<user> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("insert into user(endYear,citylng,citylat,intensity,sector,topic,insight,swot,url,region,startYear,impact,added,published,city,country,relevance,pestle,source,title,liklihood) values(:endYear,:citylng,:citylat,:intensity,:sector,:topic,:insight,:swot,:url,:region,:startYear,:impact,:added,:published,:city,:country,:relevance,:pestle,:source,:title,:liklihood)");
        writer.setDataSource(this.dataSource);
        return writer;
    }
    @Bean
    public Job importUserJob(){

        return this.jobBuilderFactory.get("USER-IMPORT-JOB")
                .increment(new RunIdIncrementer())
                .end()
                .build();

    }
    @Bean
    public Step step1(){
    return this.stepBuildFactory.get("step1")
    .<User,User>chunk(chunkSize:10)
    .reader(reader())
            .process(process())
            .writer(writer())
            .build();

}
