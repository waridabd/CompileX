# Review 1 Grammar


program -> statement*


statement -> declaration | assignment


declaration -> type IDENTIFIER = expression ;


assignment -> IDENTIFIER = expression ;


type -> সংখ্যা | বাক্য


expression -> term ( ( + | - ) term )*


term -> factor ( ( * | / ) factor )*


factor -> NUMBER | STRING | IDENTIFIER | ( expression )



## Planned Syntax Errors


- সেমিকোলন দরকার
- সঠিক আইডেন্টিফায়ার দরকার
- সঠিক টাইপ দরকার
- এক্সপ্রেশন ভুল
- সমান চিহ্ন (=) দরকার
- বন্ধনী ঠিক নেই