import { useState } from 'react';
import { sellersApi } from '../api/sellers';
import Alert from '../components/Alert';
import FormField from '../components/FormField';
import PageHeader from '../components/PageHeader';
import { RATING_VALUES } from '../constants/comments';
import { useSubmit } from '../hooks/useSubmit';

const INITIAL_VALUES = {
  firstName: '',
  lastName: '',
  email: '',
  message: '',
  rating: '5',
};

export default function SuggestSellerPage() {
  const [values, setValues] = useState(INITIAL_VALUES);
  const [notice, setNotice] = useState('');
  const { submit, pending, error } = useSubmit(sellersApi.requestCreation);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setValues((previous) => ({ ...previous, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const response = await submit({ ...values, rating: Number(values.rating) });
    if (response !== undefined) {
      setValues(INITIAL_VALUES);
      setNotice(response);
    }
  };

  return (
    <>
      <PageHeader
        title="Can't find a seller?"
        subtitle="Describe the seller and leave your review. An administrator will check it before publishing."
      />
      <form className="card form form--narrow" onSubmit={handleSubmit}>
        <div className="form__row">
          <FormField
            label="Seller first name"
            name="firstName"
            required
            value={values.firstName}
            onChange={handleChange}
          />
          <FormField
            label="Seller last name"
            name="lastName"
            required
            value={values.lastName}
            onChange={handleChange}
          />
        </div>
        <FormField
          label="Seller email"
          name="email"
          type="email"
          required
          value={values.email}
          onChange={handleChange}
        />
        <FormField
          as="textarea"
          label="Your comment"
          name="message"
          rows="4"
          required
          value={values.message}
          onChange={handleChange}
        />
        <FormField as="select" label="Rating" name="rating" value={values.rating} onChange={handleChange}>
          {RATING_VALUES.map((value) => (
            <option key={value} value={value}>
              {value}
            </option>
          ))}
        </FormField>
        <Alert>{error?.message}</Alert>
        <Alert variant="success">{notice}</Alert>
        <button type="submit" className="btn" disabled={pending}>
          {pending ? 'Sending…' : 'Submit'}
        </button>
      </form>
    </>
  );
}
