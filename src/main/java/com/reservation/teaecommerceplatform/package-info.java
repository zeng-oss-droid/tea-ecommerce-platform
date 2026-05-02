/**
 * 茶叶电商平台后端（Spring Boot + MyBatis）。
 * <p><b>分层约定</b></p>
 * <ul>
 *   <li>{@code controller} — HTTP 入口，路径多为 {@code /api/...}，统一响应体 {@link com.reservation.teaecommerceplatform.common.Result}</li>
 *
 *   <li>{@code service} / {@code service.impl} — 业务规则与事务；可组合多个 Mapper</li>
 *
 *   <li>{@code mapper} — MyBatis 接口；对应 SQL 在 {@code src/main/resources/mapper/*.xml}</li>
 *
 *   <li>{@code entity} — 与数据库表字段对应的 POJO</li>
 *
 *   <li>{@code dto} — 入参封装（校验注解多在此层）</li>
 *
 *   <li>{@code config} — Spring MVC、跨域、Redis 等基础设施</li>
 *
 *   <li>{@code interceptor} — {@code JwtInterceptor} 解析 Token 写入 request 属性 {@code userId}；{@code AdminInterceptor} 限制管理端与部分写接口</li>
 * </ul>
 */
package com.reservation.teaecommerceplatform;
