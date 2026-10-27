import { useId } from 'react';

export default function FormField({ label, as: Component = 'input', children, ...inputProps }) {
  const id = useId();

  return (
    <div className="field">
      <label className="field__label" htmlFor={id}>
        {label}
      </label>
      <Component id={id} className="field__control" {...inputProps}>
        {children}
      </Component>
    </div>
  );
}
