import json
import sys


def main():
    data = json.load(sys.stdin)
    print(data)
    print("::warning file=app.js,line=1,col=5::Missing semicolon")

if __name__ == "__main__":
    main()