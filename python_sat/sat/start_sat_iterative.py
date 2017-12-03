import pycosat
import sys


def parse_clauses_from_stdin(first_line):
    var_count, clause_count = [int(x) for x in first_line.split()[2:4]]
    clauses = []
    line = sys.stdin.readline()
    while line != "END\n":
        clauses.append([int(x) for x in line.split()])
        line = sys.stdin.readline()
    assert clause_count == len(clauses)
    return clauses


def main():
    print("HELLO")
    sys.stdout.flush()
    line = sys.stdin.readline()
    while line != "STOP\n":
        clauses = parse_clauses_from_stdin(line)
        res = pycosat.solve(clauses)
        if isinstance(res, list):
            print("RESULT:")
            print(" ".join([str(x) for x in res]))
            print("END")
            sys.stdout.flush()
        else:
            print(res)
            sys.stdout.flush()
        line = sys.stdin.readline()


main()
