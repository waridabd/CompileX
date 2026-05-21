<program> ::= <statement_list> EOF

<statement_list> ::= <statement> <statement_list>
                   | ε

<statement> ::= <declaration>
              | <assignment>
              | <if_statement>

<declaration> ::= <type> IDENTIFIER ASSIGN <expression> SEMICOLON

<assignment> ::= IDENTIFIER ASSIGN <expression> SEMICOLON

<if_statement> ::= IF LPAREN <condition> RPAREN LBRACE <statement_list> RBRACE <else_part>

<else_part> ::= ELSE LBRACE <statement_list> RBRACE
              | ε

<condition> ::= <expression> <comparison_operator> <expression>
              | <expression>

<expression> ::= <term> <expression_prime>

<expression_prime> ::= PLUS <term> <expression_prime>
                     | MINUS <term> <expression_prime>
                     | ε

<term> ::= <factor> <term_prime>

<term_prime> ::= MULTIPLY <factor> <term_prime>
               | DIVIDE <factor> <term_prime>
               | ε

<factor> ::= NUMBER
           | STRING
           | IDENTIFIER
           | LPAREN <condition> RPAREN

<type> ::= TYPE_SHONGKHA
        | TYPE_BAKKO

<comparison_operator> ::= EQ
                        | NEQ
                        | LT
                        | GT
                        | LTE
                        | GTE