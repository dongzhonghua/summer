package xyz.dsvshx.ioc.user;


import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.annotation.Autowired;
import xyz.dsvshx.ioc.annotation.mvc.GetMapping;
import xyz.dsvshx.ioc.annotation.mvc.PathVariable;
import xyz.dsvshx.ioc.annotation.mvc.PostMapping;
import xyz.dsvshx.ioc.annotation.mvc.RequestBody;
import xyz.dsvshx.ioc.annotation.mvc.RequestParam;
import xyz.dsvshx.ioc.annotation.mvc.RestController;

/**
 * @author dongzhonghua
 * Created on 2021-03-13
 */
@Slf4j
@RestController("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public User get(@RequestParam(value = "name", require = true, defaultValue = "default name") String name,
            @RequestParam("des") String des,
            @RequestParam("age") Integer age) {
        return new User(name, des, age);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable("id") Integer id) {
        return userService.get(id);
    }

    @PostMapping
    public void create(@RequestBody UserDto userDto) {
        log.info(String.valueOf(userService.create(userDto)));
    }
}
