### 构建短链接生成器

实现方式：

- [x] 使用 `Guava` 中 `murmurHash3`的实现对原有链接进行 `hash`运算得出短链接值
- [x] 使用数据库层面（示例代码中使用`AtomicInteger`来模仿自增量，实际项目中可使用`NoSQL`来实现）的自增量充当发号器角色，获取唯一短链接值（十进制数字转base62）

