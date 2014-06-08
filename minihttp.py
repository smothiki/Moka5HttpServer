#!/usr/bin/env python
#
# http.py - An even simpler HTTP server than SimpleHTTPServer (but
# loosely based on it)

try:
    # Python 3
    from http.server import HTTPServer, BaseHTTPRequestHandler
    from urllib.parse import unquote
except ImportError:
    # Python 2
    from BaseHTTPServer import HTTPServer, BaseHTTPRequestHandler
    from urllib import unquote
import os
import shutil

class Server(HTTPServer):
    def __init__(self, configfile):
        config = open(configfile)
        for line in config:
            key, value = line.strip().split("=")
            if key == "port":
                port = int(value)
            elif key == "root":
                self.root = value

        HTTPServer.__init__(self, ("127.0.0.1", port), RequestHandler)

class RequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        path = self.get_path()
        f = None

        try:
            f = open(path, "rb")
        except IOError:
            self.send_error(404, "File not found")
            return

        self.send_response(200)
        ctype = self.lookup_content_type(path)
        self.send_header("Content-type", ctype)
        fs = os.fstat(f.fileno())
        self.send_header("Content-length", str(fs[6]))
        self.end_headers()
        
        shutil.copyfileobj(f, self.wfile)

    def get_path(self):
        path = unquote(self.path).replace('/', os.path.sep)[1:]
        return os.path.join(self.server.root, path)

    def lookup_content_type(self, path):
        if path[-4:] == ".css":
            return "text/css"
        elif path[-4:] == ".png":
            return "image/png"
        elif path[-5:] == ".html":
            return "text/html"
        else:
            return "text/plain"

if __name__ == "__main__":
    server = Server("config.txt")
    server.serve_forever()
