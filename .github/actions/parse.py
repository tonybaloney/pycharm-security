import json
import sys


def main():
    data = json.load(sys.stdin)
    if 'problems' in data:
        for problem in data['problems']:
            fn = problem['file'].replace('file:///github/workspace/', '')
            line = problem['line']
            col = problem['offset']
            description = problem['description']
            print(f"::warning file={fn},line={line},col={col}::{description}")

if __name__ == "__main__":
    main()