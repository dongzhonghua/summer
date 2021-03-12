package xyz.dsvshx.ioc.context;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
// public class JsonApplicationContext extends AbstractBeanFactory {
//
//     private String fileName;
//
//     public JsonApplicationContext(String fileName) {
//         this.fileName = fileName;
//     }
//
//     public void init() {
//         loadFile();
//     }
//
//     private void loadFile() {
//         InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
//         List<BeanDefinition> beanDefinitions = JsonUtils.readValue(is, new TypeReference<List<BeanDefinition>>() {
//         });
//         if (beanDefinitions != null && !beanDefinitions.isEmpty()) {
//             for (BeanDefinition beanDefinition : beanDefinitions) {
//                 registerBeanDefinition(beanDefinition.getBeanClassName(), beanDefinition);
//             }
//         }
//     }
// }
