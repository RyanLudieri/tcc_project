import * as React from "react"

import { cn } from "@/lib/utils"

const Input = React.forwardRef(({ className, type, as, ...props }, ref) => {
  let Comp = as || "input"; 
  
  if (type === "textarea" && !as) {
    Comp = "textarea";
  }

  return (
    (<Comp
      type={type === "textarea" ? undefined : type} 
      className={cn(
        "flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50",
         Comp === "textarea" && "min-h-[80px] h-auto", 
        className
      )}
      ref={ref}
      {...props} />)
  );
})
Input.displayName = "Input"

export { Input }