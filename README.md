# OpenTracing 
---

## 概要
--
OpenTracing 高度抽象了打点的API，为侵入式和非侵入式打点记录数据，提供了一套完整的API。同时为
运行上下文，Span父子关系，Header的添加，Header的解析等提供了一套完善的工具。

OpenTracing 并没有实现采样规则， 发送机制和数据格式，采样规规则由厂商自己实现。
