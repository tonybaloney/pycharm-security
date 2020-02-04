import json
import sys


def main():
    data = json.load(sys.stdin)
    cnt = 0
    if 'problems' in data:
        for problem in data['problems']:
            fn = problem['file'].replace('file://$PROJECT_DIR$/', '')
            line = problem['line']
            col = problem['offset']
            description = problem['description']
            print(f"::warning file={fn},line={line},col={col}::{description}")
            cnt += 1

    if cnt > 0:
        exit(1)
    else:
        exit(0)

if __name__ == "__main__":
    main()