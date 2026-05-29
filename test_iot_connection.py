#!/usr/bin/env python3
"""华为云 IoTDA MQTT 连通性测试"""

import socket
import ssl
import time
import sys

HOST = "21158429fd.st1.iotda-device.cn-north-4.myhuaweicloud.com"
PORT = 443
INSTANCE_ID = "9e42696f-9665-457d-a581-b63655d1d5bb"

def test_dns(host):
    """测试 DNS 解析"""
    print(f"[1/4] DNS 解析: {host}")
    try:
        addrs = socket.getaddrinfo(host, PORT, socket.AF_UNSPEC, socket.SOCK_STREAM)
        for addr in addrs:
            family = "IPv4" if addr[0] == socket.AF_INET else "IPv6"
            ip = addr[4][0]
            print(f"  ✅ {family}: {ip}")
        return addrs[0]
    except Exception as e:
        print(f"  ❌ DNS 解析失败: {e}")
        return None

def test_tcp(host, port):
    """测试 TCP 连接"""
    print(f"\n[2/4] TCP 连接: {host}:{port}")
    try:
        sock = socket.create_connection((host, port), timeout=5)
        print(f"  ✅ TCP 连接成功 ({sock.getsockname()[0]}:{sock.getsockname()[1]} → {sock.getpeername()[0]}:{sock.getpeername()[1]})")
        return sock
    except Exception as e:
        print(f"  ❌ TCP 连接失败: {e}")
        return None

def test_tls(sock, host):
    """测试 TLS 握手"""
    print(f"\n[3/4] TLS 握手")
    try:
        ctx = ssl.create_default_context()
        tls_sock = ctx.wrap_socket(sock, server_hostname=host)
        cert = tls_sock.getpeercert()
        print(f"  ✅ TLS 握手成功")
        print(f"  📜 证书 CN: {cert.get('subject', [[('未知','')]])[0][0][1]}")
        print(f"  📜 签发者: {cert.get('issuer', [[('未知','')]])[0][0][1]}")
        print(f"  🔒 加密: {tls_sock.cipher()[0]} TLSv{tls_sock.cipher()[1].split('.')[0]}")
        return tls_sock
    except Exception as e:
        print(f"  ❌ TLS 失败: {e}")
        return None

def test_websocket(tls_sock, host):
    """测试 WebSocket 升级握手"""
    print(f"\n[4/4] WebSocket 升级 (MQTT over WebSocket)")
    try:
        ws_key = "dGhlIHNhbXBsZSBub25jZQ=="
        request = (
            f"GET /mqtt HTTP/1.1\r\n"
            f"Host: {host}:{PORT}\r\n"
            f"Upgrade: websocket\r\n"
            f"Connection: Upgrade\r\n"
            f"Sec-WebSocket-Key: {ws_key}\r\n"
            f"Sec-WebSocket-Version: 13\r\n"
            f"\r\n"
        )
        tls_sock.send(request.encode())
        response = tls_sock.recv(1024).decode()
        status_line = response.split("\r\n")[0]
        print(f"  📡 响应: {status_line}")
        if "101" in status_line:
            print(f"  ✅ WebSocket 升级成功 - MQTT 连接可用！")
            print(f"\n  🎉 华为云 IoTDA MQTT 端点确认可用！")
            return True
        elif "401" in status_line:
            print(f"  ⚠️  需要认证 - 端点可用，需提供正确的 device_id 和 device_secret")
            return True
        elif "403" in status_line:
            print(f"  ⚠️  权限拒绝 - 端点可用，需注册设备并配置 device_secret")
            return True
        else:
            print(f"  ⚠️  响应码 {status_line.split()[1]} - 端点在线但可能需要特定参数")
            return True
    except Exception as e:
        print(f"  ❌ WebSocket 测试失败: {e}")
        return False

if __name__ == "__main__":
    print("=" * 60)
    print("  华为云 IoTDA 连通性测试")
    print(f"  实例: {INSTANCE_ID}")
    print(f"  端点: {HOST}:{PORT}")
    print("=" * 60)

    addr = test_dns(HOST)
    if not addr:
        sys.exit(1)

    sock = test_tcp(HOST, PORT)
    if not sock:
        sys.exit(1)

    tls_sock = test_tls(sock, HOST)
    if not tls_sock:
        sock.close()
        sys.exit(1)

    result = test_websocket(tls_sock, HOST)

    tls_sock.close()

    print("\n" + "=" * 60)
    if result:
        print("  测试结论: 华为云 IoTDA 端点连通 ✅")
        print("  下一步: 注册设备 → 获取 device_id + secret")
        print("  → 在 APP 设置页配置后即可使用")
    else:
        print("  测试结论: 需要进一步排查 ⚠️")
    print("=" * 60)
